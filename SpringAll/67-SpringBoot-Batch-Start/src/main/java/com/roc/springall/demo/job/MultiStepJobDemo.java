package com.roc.springall.demo.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * @Description MultiStepJobDemo
 * @Author dongp
 * @Date 2022/7/25 0025 17:28
 */
@Component
public class MultiStepJobDemo {
    @Autowired
    private JobBuilderFactory jobBuilderFactory;
    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    public Job multiStopJob() {
        return jobBuilderFactory.get("multiStopJob")
                .start(step1())
                .next(step2())
                .next(step3())
                .build();
    }

    private Step step1() {
        return stepBuilderFactory.get("step1")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("执行步骤一操作...");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    private Step step2() {
        return stepBuilderFactory.get("step2")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("执行步骤二操作...");
                    return RepeatStatus.FINISHED;
                })).build();
    }

    private Step step3() {
        return stepBuilderFactory.get("step3")
                .tasklet(((stepContribution, chunkContext) -> {
                    System.out.println("执行步骤三操作...");
                    return RepeatStatus.FINISHED;
                })).build();
    }
}
