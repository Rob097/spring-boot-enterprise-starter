package com.rob.uiapi.controllers;

import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rob.core.database.RoleSearchCriteria;
import com.rob.core.database.UserSearchCriteria;
import com.rob.core.fetch.RoleFetchHandler;
import com.rob.core.fetch.modules.FetchBuilder;
import com.rob.core.models.SYS.Role;
import com.rob.core.models.SYS.User;
import com.rob.core.models.enums.PropertiesEnum;
import com.rob.core.repositories.IRoleRepository;
import com.rob.core.repositories.IUserRepository;
import com.rob.core.services.IUserService;
import com.rob.core.utils.Properties;
import com.rob.core.utils.java.IntegerList;
import com.rob.security.jwt.JwtUtils;
import com.rob.security.payloads.request.LoginRequest;
import com.rob.security.payloads.request.SignupRequest;
import com.rob.security.payloads.response.JwtResponse;
import com.rob.security.payloads.response.MessageResponse;
import com.rob.uiapi.dto.mappers.UserMapper;
import com.rob.uiapi.dto.models.UserR;

@CrossOrigin(origins = "*", maxAge = 3600, allowCredentials = "false")
@RestController
@RequestMapping("/api/auth")
public class AuthRS {

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private PasswordEncoder encoder;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private IUserService userService;

	@Autowired
	private IUserRepository userRepository;

	@Autowired
	private IRoleRepository roleRepository;

	@Autowired
	private UserMapper userMapper;
	
	private Properties mainProperties = new Properties(PropertiesEnum.MAIN_PROPERTIES.getName());


	/**
	 * Used when a user try to login. It check if the params are ok and if they are,
	 * it creates and response with a JWT token.
	 * 
	 * @param loginRequest : Encapsulation of main parameters used to login
	 *                     (username and password).
	 * @param response
	 * @return JwtResponse with the token and the roles.
	 * @throws SQLException
	 */
	@PostMapping("/signin")
	public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
			HttpServletResponse response) throws SQLException {
		Authentication authentication;
		User user = null;

		try {

			authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			user = (User) userService.loadUserByUsername(loginRequest.getUsername());

		} catch (AuthenticationException e) {
			e.printStackTrace();
			return ResponseEntity.badRequest()
					.body(new MessageResponse("Attenzione! Le credenziali non sono corrette"));
		}

		SecurityContextHolder.getContext().setAuthentication(authentication);
		String token = jwtUtils.generateJwtToken(authentication, loginRequest.isRememberMe(), "" + user.getId());

		User userDetails = (User) authentication.getPrincipal();
		RoleSearchCriteria criteria = new RoleSearchCriteria();
		criteria.setUserId(userDetails.getId());
		FetchBuilder fetchBuilder = new FetchBuilder();
		fetchBuilder.addOption(RoleFetchHandler.FETCH_PERMISSIONS);
		criteria.setFetch(fetchBuilder.build());
		List<Role> roles = roleRepository.findByCriteria(criteria);

		return ResponseEntity.ok(new JwtResponse(token, "" + userDetails.getId(), userDetails.getUsername(),
					userDetails.getEmail(), roles));

	}

	/**
	 * Method used when a token is going to expire and a user chose to refresh it.
	 * 
	 * @param request
	 * @param response
	 * @return Bad request if something went wrong or an JwtResponse with the new
	 *         token
	 */
	@RequestMapping(value = "/refresh-token", method = RequestMethod.GET)
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request,
			HttpServletResponse response) {

		// System.out.println("HEADERS: " + request.getHeader("authorization"));
		String authToken = request.getHeader(mainProperties.getProperty(PropertiesEnum.TOKEN_HEADER.getName()));
		boolean rememberMe = false;

		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			check: for (Cookie cookie : cookies) {
				if (cookie.getName().equals("rememberMe") && cookie.getValue().equals("true")) {
					rememberMe = true;
					break check;
				}
			}
		}

		if (authToken != null) {
			final String token = authToken.substring(7);

			if (jwtUtils.canTokenBeRefreshed(token)) {
				String refreshedToken = jwtUtils.refreshToken(token, rememberMe);

				response.setHeader(mainProperties.getProperty(PropertiesEnum.TOKEN_HEADER.getName()), refreshedToken);

				response.setHeader("exp", jwtUtils.getExpirationDateFromToken(refreshedToken).toString());

				return ResponseEntity.ok(new JwtResponse(refreshedToken));

			}

		}

		return ResponseEntity.badRequest().body(null);
	}

	/**
	 * Method used when a user want to sign up to the application. It also check the
	 * roles.
	 * 
	 * @param signUpRequest
	 * @param request
	 * @return badRequest if something went wrong or a ok response instead.
	 */
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest, HttpServletRequest request) {

		try {

			UserSearchCriteria criteria = new UserSearchCriteria();
			criteria.setUsername(signUpRequest.getUsername());
			Validate.isTrue(userRepository.findSingleByCriteria(criteria) == null, "Error: Username is already taken!");

			criteria = new UserSearchCriteria();
			criteria.setEmail(signUpRequest.getEmail());
			Validate.isTrue(userRepository.findSingleByCriteria(criteria) == null, "Error: Email is already in use!");

			// Create new user's account
			UserR userR = new UserR(signUpRequest.getUsername(), signUpRequest.getEmail(),
					encoder.encode(signUpRequest.getPassword()));

			User user = userMapper.map(userR);

			IntegerList strRoles = new IntegerList();
			strRoles.addAll(signUpRequest.getRoles());
			RoleSearchCriteria roleCriteria = new RoleSearchCriteria();
			roleCriteria.setIds(strRoles);
			List<Role> roles = roleRepository.findByCriteria(roleCriteria);

			user.setRoles(roles);

			// Save user into user collection in general DB for authentication
			user = userService.create(user);

			return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.badRequest().body(new MessageResponse("User registration failed!"));
		}
	}

}
