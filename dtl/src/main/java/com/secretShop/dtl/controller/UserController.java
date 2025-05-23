package com.secretShop.dtl.controller;

import com.secretShop.dtl.DTO.UpdateBalanceDTO;
import com.secretShop.dtl.DTO.UserDTO;
import com.secretShop.dtl.enums.WorkGroup;
import com.secretShop.dtl.service.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.create(userDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable UUID id, @RequestBody UserDTO userDTO) {
        userDTO.setUserId(id);
        return ResponseEntity.ok(userService.save(userDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID userId) {
        userService.deleteById(userId);
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/balance")
    public ResponseEntity<Void> updateUserBalance(@RequestBody UpdateBalanceDTO balanceDTO) {
        userService.updateBalance(balanceDTO);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/work-groups")
    public ResponseEntity<WorkGroup[]> getAvailableWorkGroups() {
        return ResponseEntity.ok(WorkGroup.values());
    }

    @GetMapping("/work-group/{workGroup}")
    public ResponseEntity<List<UserDTO>> getUsersByWorkGroup(
            @PathVariable("workGroup") String workGroup) {
        return ResponseEntity.ok(userService.findByWorkGroup(workGroup));
    }
}