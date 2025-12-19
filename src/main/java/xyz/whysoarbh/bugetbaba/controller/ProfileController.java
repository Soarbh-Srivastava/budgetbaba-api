package xyz.whysoarbh.bugetbaba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import xyz.whysoarbh.bugetbaba.dto.AuthDTO;
import xyz.whysoarbh.bugetbaba.dto.ProfileDTO;
import xyz.whysoarbh.bugetbaba.service.ProfileService;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ProfileController
{
    private final ProfileService profileService;

    @PostMapping("/register")
    public ResponseEntity<ProfileDTO> registerProfile(@RequestBody ProfileDTO profileDTO)
    {
        ProfileDTO registeredProfile = profileService.registerProfile(profileDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredProfile);
    }

    @GetMapping("/activate")
    public ResponseEntity<String> activateProfile(@RequestParam String token)
    {
        boolean isActivated = profileService.activateProfile(token);
        if(isActivated)
        {
            return ResponseEntity.ok("Profile is activated");
        }
        else
        {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Activation token not found or already used");
        }
    }
    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody AuthDTO authDTO) {
        try {
            if (!profileService.isAccountActive(authDTO.getEmail())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                        Map.of("message", "Account is not active. Please activate your account first")
                );
            }

            Map<String,Object> response = profileService.authenticateAndGenrateToken(authDTO);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Map.of("error", e.getMessage())
            );
        }
    }


}
