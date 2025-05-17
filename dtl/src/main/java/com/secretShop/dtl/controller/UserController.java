package com.secretShop.dtl.controller;

import com.secretShop.dtl.DTO.UpdateBalanceDTO;
import com.secretShop.dtl.entity.User;
import com.secretShop.dtl.repository.UserRepository;
import com.secretShop.dtl.repository.UsersQuestsRepository;
import com.secretShop.dtl.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final UserService userService;

    @GetMapping("/balance/{userId}")
    public Integer getBalance(@PathVariable("userId") UUID userId) {
        return userRepository.getBalanceById(userId);
    }

    @PatchMapping("/balance/update")
    public void updateBalance(@RequestBody UpdateBalanceDTO balanceDTO) {
        userService.updateBalance(balanceDTO);
    }

    @GetMapping("/user")
    public PagedModel<User> getAll(Pageable pageable) {
        Page<User> users = userRepository.findAll(pageable);
        return new PagedModel<>(users);
    }
}
