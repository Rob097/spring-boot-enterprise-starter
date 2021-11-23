package com.rob.uiapi.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rob.core.database.UserSearchCriteria;
import com.rob.core.exceptions.UserReadableException;
import com.rob.core.fetch.UserFetchHandler;
import com.rob.core.fetch.modules.Fetch;
import com.rob.core.fetch.modules.FetchBuilder;
import com.rob.core.models.SYS.User;
import com.rob.core.repositories.IUserRepository;
import com.rob.core.services.IUserService;
import com.rob.core.utils.db.Range;
import com.rob.core.utils.db.RangeUtils;
import com.rob.core.utils.java.messages.Message;
import com.rob.core.utils.java.messages.MessageResource;
import com.rob.core.utils.java.messages.MessageResources;
import com.rob.uiapi.controllers.views.IView;
import com.rob.uiapi.controllers.views.Normal;
import com.rob.uiapi.controllers.views.Verbose;
import com.rob.uiapi.dto.mappers.UserMapper;
import com.rob.uiapi.dto.mappers.UserRMapper;
import com.rob.uiapi.dto.models.UserQ;
import com.rob.uiapi.dto.models.UserR;
import com.rob.uiapi.utils.MetadataResource;
import com.rob.uiapi.utils.PatchOperation;
import com.rob.uiapi.utils.Sort;
import com.rob.uiapi.utils.UIApiConstants;
import com.rob.uiapi.utils.UIApiRS;
import com.rob.uiapi.utils.UIApiUtils;


@RestController
@RequestMapping("/user")
public class UserRS implements UIApiRS<UserR, UserQ> {

	@Autowired
	private IUserService userService;
	
	@Autowired
	private IUserRepository userRepository;
	
	@Autowired
	private UserRMapper userRMapper;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private ObjectMapper jacksonObjectMapper;
	
	@Autowired
	private PasswordEncoder encoder;
	
	/*@Autowired
	private UserMapper userMapper;*/
	
	private static final int MAX_RESULT_SIZE = 100;
	private static final int DEFAULT_RESULT_SIZE = 20;

	
	@Override
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResources<UserR>> find(
			UserQ parameters,
			@RequestParam(name = SORT, required = false) Sort sort,
			@RequestParam(name = VIEW, required = false, defaultValue = Normal.name) IView view, 
			@RequestHeader(name = RANGE, required = false) Range requestedRange
	) throws Exception {


		//Limitazione automatica dei risultati
		Range range = RangeUtils.limit(requestedRange, MAX_RESULT_SIZE, DEFAULT_RESULT_SIZE, UIApiConstants.RANGE_ITEMS_UNIT);

		UserSearchCriteria criteria = new UserSearchCriteria();
		criteria.setFetch(applyViewCriteria(view));
		criteria.setRange(range);
		
		criteria = applySearchCriteria(parameters, criteria);
		Validate.isTrue(criteria.isValidCriteria(), "Almeno un parametro di ricerca deve essere valorizzato");
		
		List<User> list = userRepository.findByCriteria(criteria);
		List<UserR> result = list.stream().map(userRMapper::map).collect(Collectors.toList());

		return UIApiUtils.buildRangeAwareSuccessResponse(result, range, requestedRange);

	}

	@Override
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResource<UserR>> get(
			@PathVariable("id") String id, 
			UserQ parameters, 
			@RequestParam(name = VIEW, required = false) IView view
	) throws Exception {
		Validate.notEmpty(id, "Parametro obbligatorio mancante: id.");
		
		User user = userRepository.findById(id, applyViewCriteria(view));
		
		if (user == null) {
			throw new NotFoundException("User con id " + id + " non trovato");
		}
		
		UserR userR = userRMapper.map(user);
		
		MessageResource<UserR> result = new MessageResource<UserR>(userR);
		
		return new ResponseEntity<MessageResource<UserR>>(result, HttpStatus.OK);
	}

	@Override
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResource<UserR>> create(@RequestBody MetadataResource<UserR> resource) throws Exception {
		throw new UnsupportedOperationException();
		//Implemented in AuthRS
	}

	@Override
	@PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResource<UserR>> update(@PathVariable("id") String id, @RequestBody MetadataResource<UserR> resource) throws Exception {
		Validate.notNull(resource, "Nessuna risorsa passata come parametro");
		UserR input = resource.getContent();
		Validate.notNull(input, "Nessuna risorsa passata come parametro nel body");
		
		User data = userMapper.map(input);
		
		// I don't want to update these fields by put requests, only patch.
		data.setUsername(null);
		data.setEmail(null);
		data.setPassword(null);
		
		data = userService.update(data);
		
		if (data != null) {
			MessageResource<UserR> result = new MessageResource<>(userRMapper.map(data));
			return new ResponseEntity<MessageResource<UserR>>(result, HttpStatus.OK);
		}
		
		throw new UserReadableException("Errore durante l'aggiornamento dell'utente.");
	}
	
	/**
	 * Tramite questo metodo è possibile aggiornare in maniera atomica alcune informazione dell'utente. Attualmente sono supportate queste
	 * operazioni
	 * <ul>
	 * <li>{"op":"replace", "path":"/password", "value":[....]}</li>
	 * </ul>
	 *
	 * @param id
	 * @param operations
	 * @return
	 * @throws Exception
	 */
	@PatchMapping(path = "/{id}", consumes = "application/json-patch+json", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResource<UserR>> patch(@PathVariable("id") String id, @RequestBody List<PatchOperation> operations) throws Exception {
		Validate.notEmpty(operations, "Nessuna operazione passata come parametro");

		List<Message> messages = new ArrayList<>();
		boolean isToUpdate = false;

		User user = userRepository.findById(id);
		if (user == null) {
			throw new NotFoundException();
		}

		for (PatchOperation operation : operations) {
			if (operation.getPath().matches("^/username") && operation.getOp() == PatchOperation.Op.replace) {
				TypeReference<String> tr = new TypeReference<String>() {
				};
				String username = jacksonObjectMapper.readValue(operation.getValue().getValue(), tr);
				user.setUsername(username);
				isToUpdate = true;
			} else if (operation.getPath().matches("^/email") && operation.getOp() == PatchOperation.Op.replace) {
				TypeReference<String> tr = new TypeReference<String>() {
				};
				String email = jacksonObjectMapper.readValue(operation.getValue().getValue(), tr);
				user.setEmail(email);
				isToUpdate = true;
			} else if (operation.getPath().matches("^/password") && operation.getOp() == PatchOperation.Op.replace) {
				TypeReference<String> tr = new TypeReference<String>() {
				};
				String password = jacksonObjectMapper.readValue(operation.getValue().getValue(), tr);
				user.setPassword(encoder.encode(password));
				isToUpdate = true;
			}
		}

		Message message = null;
		if(isToUpdate) {
			user = userService.update(user);
			message = new Message("Aggiornamento effettuato con successo.");
		} else {
			message = new Message("Nessun aggiornamento effettuato.");
		}
		messages.add(message);

		UserR userR = userRMapper.map(user);

		return new ResponseEntity<>(new MessageResource<>(userR, messages), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<MessageResources<UserR>> deleteAll(UserQ parameters, IView view) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseEntity<MessageResource<UserR>> delete(String id, UserQ parameters, IView view) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	private UserSearchCriteria applySearchCriteria(UserQ parameters, UserSearchCriteria criteria) {
		if (parameters == null) {
			return criteria;
		}
		
		if (StringUtils.isNotBlank(parameters.getId())) {
			criteria.setId(parameters.getId());
		}
		
		if (StringUtils.isNotBlank(parameters.getUsername())) {
			criteria.setUsername(parameters.getUsername());
		}
		
		return criteria;
		
	}
	
	/**
	 * Inizializzazione fetch
	 * @param view
	 * @return
	 */
	private Fetch applyViewCriteria(IView view) {
		if (view == null) {
			return FetchBuilder.none();
		}

		FetchBuilder fetchBuilder = new FetchBuilder();
		
		if (view.isAtLeast(Normal.value)) {
			fetchBuilder.addOption(UserFetchHandler.FETCH_ROLES);
		}
		if (view.isAtLeast(Verbose.value)) {
			fetchBuilder.addOption(UserFetchHandler.FETCH_ROLES_PERMISSIONS);
		}
		
		return fetchBuilder.build();
	}

}
