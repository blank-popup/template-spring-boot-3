package com.example.template.setting.init;

import java.util.List;
import java.util.Set;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RunInitialization implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<String> nonOptionArgs = args.getNonOptionArgs();
        log.info("Size of nonOptionArgs : {}", nonOptionArgs.size());
        for (int ii = 0; ii < nonOptionArgs.size(); ++ii) {
            log.info("nonOptionArgs[{}] : {}", ii, nonOptionArgs.get(ii));
        }

        Set<String> optionNames = args.getOptionNames();
        log.info("Size of optionNames : {}", optionNames.size());
        optionNames.forEach(set -> {
            log.info("{} : {}", set, args.getOptionValues(set));
        });

        String[] sourceArgs = args.getSourceArgs();
        log.info("Length of sourceArgs : {}", sourceArgs.length);
        for (int ii = 0; ii < sourceArgs.length; ++ii) {
            log.info("sourceArgs[{}] : {}", ii, sourceArgs[ii]);
        }

        log.trace("Logging Test : TRACE Level");
        log.debug("Logging Test : DEBUG Level");
        log.info("Logging Test : INFO Level");
        log.warn("Logging Test : Warn Level");
        log.error("Logging Test : ERROR Level");
    }
}
