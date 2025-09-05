package com.nitro.ms.neg.seguridad.gestion;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;

@SpringBootApplication
@RequiredArgsConstructor
@Slf4j
public class MsNegSeguridadGestionApplication {

    private final Environment environment;

    public static void main(String[] args) {
        SpringApplication.run(MsNegSeguridadGestionApplication.class, args);
    }

    // Este método se ejecutará una vez que la aplicación haya arrancado.
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        // Comprueba si el perfil 'local' está activo
        if (environment.acceptsProfiles(org.springframework.core.env.Profiles.of("local"))) {
            // Si es así, imprime una advertencia muy visible en los logs.
            log.warn("""
                    
                    **************************************************
                    *  ⚠️  ENTORNO 'local' ACTIVO: Seguridad desactivada
                    *  Solo usar para desarrollo. No usar en producción.
                    **************************************************
                    """);
        }
    }

}
