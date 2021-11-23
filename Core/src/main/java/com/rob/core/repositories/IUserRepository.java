package com.rob.core.repositories;

import java.sql.SQLException;
import java.util.List;

import com.rob.core.database.UserSearchCriteria;
import com.rob.core.fetch.modules.Fetch;
import com.rob.core.models.SYS.User;

public interface IUserRepository {
	
	User findById(String id) throws SQLException;

	User findById(String id, Fetch fetch) throws SQLException;
	
	User findSingleByCriteria(UserSearchCriteria criteria) throws SQLException;
	
	List<User> findByCriteria(UserSearchCriteria criteria) throws SQLException;
	
	User create(User data) throws SQLException;
	
	void createRoleRelation(int userId, int roleId) throws SQLException;
	
	User update(User data) throws SQLException;
	
}
