package com.word_trainer.security.services;



import com.word_trainer.domain.entity.User;
import com.word_trainer.repository.UserRepository;
import com.word_trainer.security.domain.AuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username)  {

        User user = repository.findByEmailAndIsActiveTrue(username)
                .orElseThrow(()-> new UsernameNotFoundException(
                        String.format("User with email %s not found", username)));

        return new AuthenticatedUser(user);
    }
}
