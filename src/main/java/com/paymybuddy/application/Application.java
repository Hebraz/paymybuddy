package com.paymybuddy.application;

import com.paymybuddy.application.model.ConnectionTransfer;
import com.paymybuddy.application.model.User;
import com.paymybuddy.application.repository.TransactionRepository;
import com.paymybuddy.application.repository.UserRepository;
import com.paymybuddy.application.service.TransactionService;
import com.paymybuddy.application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
