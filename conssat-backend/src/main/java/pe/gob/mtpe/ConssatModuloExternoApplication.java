package pe.gob.mtpe;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class ConssatModuloExternoApplication implements CommandLineRunner {
	
	@Autowired
	private BCryptPasswordEncoder encriptarclave;
	
    Logger log = LoggerFactory.getLogger(this.getClass()); 

	public static void main(String[] args) {
		SpringApplication.run(ConssatModuloExternoApplication.class, args);	
	}

	@Override
	public void run(String... args) throws Exception { 
		String clave = "12345";
		log.error("===================="+clave);
		for(int i= 0;i<2;i++) {
			String claveEncriptada = encriptarclave.encode(clave);
			//System.out.println("=============="+claveEncriptada);
		}
	}

}

