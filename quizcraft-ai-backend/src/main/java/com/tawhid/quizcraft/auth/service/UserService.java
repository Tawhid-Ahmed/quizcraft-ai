package com.tawhid.quizcraft.auth.service;

import com.tawhid.quizcraft.auth.dto.UpdateProfileRequest;
import com.tawhid.quizcraft.auth.entity.Role;
import com.tawhid.quizcraft.auth.entity.User;
import com.tawhid.quizcraft.auth.enums.RoleType;
import com.tawhid.quizcraft.auth.repository.RoleRepository;
import com.tawhid.quizcraft.auth.repository.UserRepository;
import com.tawhid.quizcraft.common.exceptions.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public User getUserByEmail(String email){
        return userRepository.findByEmailAndDeletedAtIsNull(email)
                .orElseThrow(()-> new CustomException("User not found"));
    }

    public User getUserById(Long id){
        return userRepository.findByIdAndDeletedAtIsNull(id)
                .orElseThrow(()-> new CustomException("User not found"));
    }

    public List<String> getUserRoles(Long userId) {
        User user = getUserById(userId);
        return user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();
    }

    public void updatePassword(User user, String password){
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    public void assignRole(Long userId, RoleType roleType){
        User user = getUserById(userId);
        Role role = roleRepository.findByName(roleType)
                .orElseThrow(()-> new CustomException("Role not found"));
        user.getRoles().add(role);
        userRepository.save(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void softDeleteUser(Long userId){
        User user = getUserById(userId);
        user.setDeletedAt(OffsetDateTime.now(ZoneOffset.UTC));
        userRepository.save(user);
    }

//    public void updateProfilePicture(Long id, MultipartFile file){
//
//        User user = getUserById(id);
//        try{
//            String url = fileSotrageService.storeFile(file,'profiles/');
//            user.setProfileImageUrl(url);
//            userRepository.save(user);
//        }catch (IOException e){
//            throw new CustomException("Failed to upload profile picture",e);
//        }
//    }

    public List<User> getAllUsers(){
        return userRepository.findAllByDeletedAtIsNull();
    }

    public User updateUserProfile(Long id, UpdateProfileRequest updateProfileRequest){
        User user = getUserById(id);
        if(updateProfileRequest.getName()!= null) user.setName(updateProfileRequest.getName());
        if(updateProfileRequest.getProfileImageUrl()!= null) user.setProfileImageUrl(updateProfileRequest.getProfileImageUrl());
        return userRepository.save(user);
    }

}
