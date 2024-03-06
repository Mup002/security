package tmdtdemo.tmdt.service;

import tmdtdemo.tmdt.dto.request.UserRequest;
import tmdtdemo.tmdt.entity.User;
import tmdtdemo.tmdt.utils.BaseResponse;

public interface UserService {
    BaseResponse register(UserRequest request);
    User findUserByUsername(String username);

}
