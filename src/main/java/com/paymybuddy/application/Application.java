package com.paymybuddy.application;

import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.UserRepository;
import com.paymybuddy.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.transaction.Transactional;
import java.util.Optional;

@SpringBootApplication
public class Application implements CommandLineRunner {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	@Autowired
	UserService userService;

	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public void run(String... args) throws Exception {
		User user1 = new User("toto@titi.fr","pwd", "toto","titi", 160);
		User user2 = new User("tata@tutu.fr","pwd", "tata","tutu", 1500);
		user1.getConnections().add(user2);
		user1= userService.saveUser(user1);
		System.out.println("added user : " +  user1.getId());

		userRepository.delete(user1);
	}
}
