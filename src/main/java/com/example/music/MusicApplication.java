package com.example.music;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MusicApplication {

	public static void main(String[] args) {
		SpringApplication.run(MusicApplication.class, args);
	}

}


//how to run :

//mvn -s /Users/s0a0f3m/cpp/musicApp/minimal_settings.xml dependency:resolve
//mvn -s /Users/s0a0f3m/cpp/musicApp/minimal_settings.xml clean install
//mvn -s /Users/s0a0f3m/cpp/musicApp/minimal_settings.xml spring-boot:run


//eval "$(ssh-agent -s)"
//ssh-add --apple-use-keychain ~/.ssh/id_ed25519_personal
