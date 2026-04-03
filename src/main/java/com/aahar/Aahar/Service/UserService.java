package com.aahar.Aahar.Service;

import com.aahar.Aahar.DTO.CodeDTOs;
import com.aahar.Aahar.Entity.User;
import com.aahar.Aahar.Repository.UserRepo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepo userRepo;
    private final NutritionCalculatorService nutritionCalculatorService;

    public UserService(UserRepo userRepo, NutritionCalculatorService nutritionCalculatorService) {
        this.userRepo = userRepo;
        this.nutritionCalculatorService = nutritionCalculatorService;
    }

    public User getCurretUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String email = auth.getName();
        return userRepo.findByemail(email).orElseThrow(()-> new RuntimeException("User Not Found"));
    }

    public CodeDTOs.UserProfileResponse getUserProfile(String email){
        User user = userRepo.findByemail(email).orElseThrow(()-> new RuntimeException("User Not Found"));
        return makeUserProfileResponse(user);
    }

    public CodeDTOs.UserProfileResponse updateUserProfile(String email , CodeDTOs.UpdateUserProfileRequest updateRequest){
        User user = userRepo.findByemail(email).orElseThrow(()-> new RuntimeException("User Not Found"));

        if (updateRequest.getName() != null) {
            user.setName(updateRequest.getName());
        }
        if (updateRequest.getAge() != null) {
            user.setAge(updateRequest.getAge());
        }
        if (updateRequest.getHeight() != null) {
            user.setHeight(updateRequest.getHeight());
        }
        if (updateRequest.getWeight() != null) {
            user.setWeight(updateRequest.getWeight());
        }
        if (updateRequest.getActivityLevel() != null) {
            user.setActivityLevel(User.ActivityLevel.valueOf(updateRequest.getActivityLevel()));
        }
        if (updateRequest.getGoal() != null) {
            user.setGoal(User.DietGoal.valueOf(updateRequest.getGoal()));
        }
        User updatedUser = userRepo.save(user);
        return makeUserProfileResponse(updatedUser);

    }

    private CodeDTOs.UserProfileResponse makeUserProfileResponse(User user) {
        CodeDTOs.NutritionalTargets targets = nutritionCalculatorService.caltulateTargets(user);

        CodeDTOs.UserProfileResponse response =
                new CodeDTOs.UserProfileResponse();
        response.setId(user.getId());
        response.setName(user.getName());
        response.setEmail(user.getEmail());
        response.setAge(user.getAge());
        response.setGender(user.getGender().toString());
        response.setHeight(user.getHeight());
        response.setWeight(user.getWeight());
        response.setActivityLevel(user.getActivityLevel().toString());
        response.setGoal(user.getGoal().toString());
        response.setTargets(targets);

        return response;
    }
}
