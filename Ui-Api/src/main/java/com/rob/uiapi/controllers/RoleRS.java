package com.rob.uiapi.controllers;

import java.util.List;
import java.util.stream.Collectors;

import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rob.core.database.RoleSearchCriteria;
import com.rob.core.exceptions.UserReadableException;
import com.rob.core.fetch.RoleFetchHandler;
import com.rob.core.fetch.modules.Fetch;
import com.rob.core.fetch.modules.FetchBuilder;
import com.rob.core.models.SYS.Role;
import com.rob.core.repositories.IRoleRepository;
import com.rob.core.services.IRoleService;
import com.rob.core.utils.db.Range;
import com.rob.core.utils.db.RangeUtils;
import com.rob.core.utils.java.messages.MessageResource;
import com.rob.core.utils.java.messages.MessageResources;
import com.rob.uiapi.controllers.views.IView;
import com.rob.uiapi.controllers.views.Normal;
import com.rob.uiapi.dto.mappers.RoleMapper;
import com.rob.uiapi.dto.mappers.RoleRMapper;
import com.rob.uiapi.dto.models.UserQ.RoleQ;
import com.rob.uiapi.dto.models.UserR.RoleR;
import com.rob.uiapi.utils.MetadataResource;
import com.rob.uiapi.utils.Sort;
import com.rob.uiapi.utils.UIApiConstants;
import com.rob.uiapi.utils.UIApiRS;
import com.rob.uiapi.utils.UIApiUtils;

@RestController
@RequestMapping("/role")
public class RoleRS implements UIApiRS<RoleR, RoleQ> {
	
	private static final int MAX_RESULT_SIZE = 100;
	private static final int DEFAULT_RESULT_SIZE = 20;

	@Autowired
	private IRoleService roleService;
	
	@Autowired
	private IRoleRepository roleRepository;
	
	@Autowired
	private RoleRMapper roleRMapper;
	
	@Autowired
	private RoleMapper roleMapper;
	
	@Override
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResources<RoleR>> find(
			RoleQ parameters,
			@RequestParam(name = SORT, required = false) Sort sort,
			@RequestParam(name = VIEW, required = false, defaultValue = Normal.name) IView view, 
			@RequestHeader(name = RANGE, required = false) Range requestedRange
	) throws Exception {
		
		//Limitazione automatica dei risultati
		Range range = RangeUtils.limit(requestedRange, MAX_RESULT_SIZE, DEFAULT_RESULT_SIZE, UIApiConstants.RANGE_ITEMS_UNIT);
		
		RoleSearchCriteria criteria = new RoleSearchCriteria();
		criteria.setFetch(applyViewCriteria(view));
		criteria.setRange(range);
		
		criteria = applySearchCriteria(parameters, criteria);
		Validate.isTrue(criteria.isValidCriteria(), "Almeno un parametro di ricerca deve essere valorizzato");
				
		List<Role> list = roleRepository.findByCriteria(criteria);
		List<RoleR> result = list.stream().map(roleRMapper::map).collect(Collectors.toList());

		return UIApiUtils.buildRangeAwareSuccessResponse(result, range, requestedRange);
	}

	@Override
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResource<RoleR>> get(
			@PathVariable("id") String id, 
			RoleQ parameters, 
			@RequestParam(name = VIEW, required = false) IView view
	) throws Exception {
		Validate.notEmpty(id, "Parametro obbligatorio mancante: id.");
		Integer roleId = Integer.parseInt(id);
		Validate.notNull(roleId, "L'idendificativo del ruolo non è valido.");
		
		Role role = roleRepository.findById(roleId, applyViewCriteria(view));
		
		if (role == null) {
			throw new NotFoundException("Ruolo con id " + id + " non trovato");
		}
		
		RoleR roleR = roleRMapper.map(role);
		
		MessageResource<RoleR> result = new MessageResource<RoleR>(roleR);
		
		return new ResponseEntity<MessageResource<RoleR>>(result, HttpStatus.OK);
	}

	@Override
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResource<RoleR>> create(@RequestBody MetadataResource<RoleR> resource) throws Exception {
		Validate.notNull(resource, "Nessuna risorsa passata come parametro");
		RoleR input = resource.getContent();
		Validate.notNull(input, "Nessuna risorsa passata come parametro nel body");
		Validate.notNull(input.getId(), "L'identificativo è un campo obbligatorio.");
		
		Role actual = roleRepository.findById(input.getId());
		Validate.isTrue(actual==null, "Esiste già un ruolo con id + " + input.getId());
		
		Role data = roleMapper.map(input);
		
		data = roleService.create(data);
		
		if (data != null) {
			MessageResource<RoleR> result = new MessageResource<>(roleRMapper.map(data));
			return new ResponseEntity<MessageResource<RoleR>>(result, HttpStatus.OK);
		}
		
		throw new UserReadableException("Errore durante la creazione del ruolo.");
	}

	@Override
	@PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResource<RoleR>> update(@PathVariable("id") String id, @RequestBody MetadataResource<RoleR> resource) throws Exception {
		Validate.notNull(resource, "Nessuna risorsa passata come parametro");
		RoleR input = resource.getContent();
		Validate.notNull(input, "Nessuna risorsa passata come parametro nel body");
		Validate.notNull(input.getId(), "L'identificativo è un campo obbligatorio.");
		
		Role actual = roleRepository.findById(input.getId());
		Validate.isTrue(actual!=null, "Non esiste nessun ruolo con id + " + input.getId());
		
		Role data = roleMapper.map(input);
		
		data = roleService.update(data);
		
		if (data != null) {
			MessageResource<RoleR> result = new MessageResource<>(roleRMapper.map(data));
			return new ResponseEntity<MessageResource<RoleR>>(result, HttpStatus.OK);
		}
		
		throw new UserReadableException("Errore durante l'aggiornamento del ruolo.");
	}

	@Override
	public ResponseEntity<MessageResources<RoleR>> deleteAll(RoleQ parameters, IView view) throws Exception {
		throw new UnsupportedOperationException();
	}

	@Override
	public ResponseEntity<MessageResource<RoleR>> delete(String id, RoleQ parameters, IView view) throws Exception {
		throw new UnsupportedOperationException();
	}
	
	private RoleSearchCriteria applySearchCriteria(RoleQ parameters, RoleSearchCriteria criteria) {
		if (parameters == null) {
			return criteria;
		}
		
		if (parameters.getIds()!=null && !parameters.getIds().isEmpty()) {
			criteria.setIds(parameters.getIds());
		}
		
		if (parameters.getUserId()!=null) {
			criteria.setUserId(parameters.getUserId());
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
			fetchBuilder.addOption(RoleFetchHandler.FETCH_PERMISSIONS);
		}
		
		return fetchBuilder.build();
	}

}
