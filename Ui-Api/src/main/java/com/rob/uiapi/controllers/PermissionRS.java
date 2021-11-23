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

import com.rob.core.database.PermissionSearchCriteria;
import com.rob.core.exceptions.UserReadableException;
import com.rob.core.models.SYS.Permission;
import com.rob.core.repositories.IPermissionRepository;
import com.rob.core.services.IPermissionService;
import com.rob.core.utils.db.Range;
import com.rob.core.utils.db.RangeUtils;
import com.rob.core.utils.java.messages.MessageResource;
import com.rob.core.utils.java.messages.MessageResources;
import com.rob.uiapi.controllers.views.IView;
import com.rob.uiapi.controllers.views.Normal;
import com.rob.uiapi.dto.mappers.PermissionMapper;
import com.rob.uiapi.dto.mappers.PermissionRMapper;
import com.rob.uiapi.dto.models.UserQ.RoleQ.PermissionQ;
import com.rob.uiapi.dto.models.UserR.RoleR.PermissionR;
import com.rob.uiapi.utils.MetadataResource;
import com.rob.uiapi.utils.Sort;
import com.rob.uiapi.utils.UIApiConstants;
import com.rob.uiapi.utils.UIApiRS;
import com.rob.uiapi.utils.UIApiUtils;

@RestController
@RequestMapping("/permission")
public class PermissionRS implements UIApiRS<PermissionR, PermissionQ> {
	
	private static final int MAX_RESULT_SIZE = 100;
	private static final int DEFAULT_RESULT_SIZE = 20;
	
	@Autowired
	private IPermissionService permissionService;
	
	@Autowired
	private IPermissionRepository permissionRepository;
	
	@Autowired
	private PermissionRMapper permissionRMapper;
	
	@Autowired
	private PermissionMapper permissionMapper;
	
	@Override
	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResources<PermissionR>> find(
			PermissionQ parameters,
			@RequestParam(name = SORT, required = false) Sort sort,
			@RequestParam(name = VIEW, required = false, defaultValue = Normal.name) IView view, 
			@RequestHeader(name = RANGE, required = false) Range requestedRange
	) throws Exception {
		
		//Limitazione automatica dei risultati
		Range range = RangeUtils.limit(requestedRange, MAX_RESULT_SIZE, DEFAULT_RESULT_SIZE, UIApiConstants.RANGE_ITEMS_UNIT);
		
		PermissionSearchCriteria criteria = new PermissionSearchCriteria();
		criteria.setRange(range);
		
		criteria = applySearchCriteria(parameters, criteria);
		Validate.isTrue(criteria.isValidCriteria(), "Almeno un parametro di ricerca deve essere valorizzato");
				
		List<Permission> list = permissionRepository.findByCriteria(criteria);
		List<PermissionR> result = list.stream().map(permissionRMapper::map).collect(Collectors.toList());

		return UIApiUtils.buildRangeAwareSuccessResponse(result, range, requestedRange);
	}
	
	@Override
	@GetMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResource<PermissionR>> get(
			@PathVariable("id") String id, 
			PermissionQ parameters, 
			@RequestParam(name = VIEW, required = false) IView view
	) throws Exception {
		Validate.notEmpty(id, "Parametro obbligatorio mancante: id.");
		Integer roleId = Integer.parseInt(id);
		Validate.notNull(roleId, "L'idendificativo del ruolo non è valido.");
		
		Permission permission = permissionRepository.findById(roleId);
		
		if (permission == null) {
			throw new NotFoundException("Privilegio con id " + id + " non trovato");
		}
		
		PermissionR permissionR = permissionRMapper.map(permission);
		
		MessageResource<PermissionR> result = new MessageResource<PermissionR>(permissionR);
		
		return new ResponseEntity<MessageResource<PermissionR>>(result, HttpStatus.OK);
	}
	
	@Override
	@PostMapping(produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResource<PermissionR>> create(@RequestBody MetadataResource<PermissionR> resource) throws Exception {
		Validate.notNull(resource, "Nessuna risorsa passata come parametro");
		PermissionR input = resource.getContent();
		Validate.notNull(input, "Nessuna risorsa passata come parametro nel body");
		
		Permission data = permissionMapper.map(input);
		
		data = permissionService.create(data);
		
		if (data != null) {
			MessageResource<PermissionR> result = new MessageResource<>(permissionRMapper.map(data));
			return new ResponseEntity<MessageResource<PermissionR>>(result, HttpStatus.OK);
		}
		
		throw new UserReadableException("Errore durante la creazione del privilegio.");
	}
	
	@Override
	@PutMapping(path = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<MessageResource<PermissionR>> update(@PathVariable("id") String id, @RequestBody MetadataResource<PermissionR> resource) throws Exception {
		Validate.notNull(resource, "Nessuna risorsa passata come parametro");
		PermissionR input = resource.getContent();
		Validate.notNull(input, "Nessuna risorsa passata come parametro nel body");
		
		Permission data = permissionMapper.map(input);
		
		data = permissionService.update(data);
		
		if (data != null) {
			MessageResource<PermissionR> result = new MessageResource<>(permissionRMapper.map(data));
			return new ResponseEntity<MessageResource<PermissionR>>(result, HttpStatus.OK);
		}
		
		throw new UserReadableException("Errore durante la creazione del privilegio.");
	}
	
	@Override
	public ResponseEntity<MessageResources<PermissionR>> deleteAll(PermissionQ parameters, IView view)
			throws Exception {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public ResponseEntity<MessageResource<PermissionR>> delete(String id, PermissionQ parameters, IView view)
			throws Exception {
		throw new UnsupportedOperationException();
	}
	
	private PermissionSearchCriteria applySearchCriteria(PermissionQ parameters, PermissionSearchCriteria criteria) {
		if (parameters == null) {
			return criteria;
		}
		
		if (parameters.getIds()!=null && !parameters.getIds().isEmpty()) {
			criteria.setIds(parameters.getIds());
		}
		
		if (parameters.getRoleId()!=null) {
			criteria.setRoleId(parameters.getRoleId());
		}
		
		if (parameters.getUserId()!=null) {
			criteria.setUserId(parameters.getUserId());
		}
		
		return criteria;
		
	}

}
