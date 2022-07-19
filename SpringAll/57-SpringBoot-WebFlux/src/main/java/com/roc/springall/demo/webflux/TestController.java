package com.roc.springall.demo.webflux;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

/**
 * @Description TestController
 * @Author dongp
 * @Date 2022/7/19 0019 18:16
 */
@RestController
public class TestController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("sync")
    public String sync() {
        logger.info("sync method start");
        String result = this.execute();
        logger.info("sync method end");
        return result;
    }

    private String execute() {
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "hello";
    }

    @GetMapping("async/mono")
    public Mono<String> asyncMono() {
        logger.info("async method start");
        Mono<String> result = Mono.fromSupplier(this::execute);
        logger.info("async method end");
        return result;
    }

    @GetMapping(value = "async/flux", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> asyncFlux() {
        logger.info("async method start");
        Flux<String> result = Flux.fromStream(IntStream.range(1, 5).mapToObj(i -> {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "int value：" + i;
        }));
        logger.info("async method end");
        return result;
    }
}
