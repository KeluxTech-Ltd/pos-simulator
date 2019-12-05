//package com.jayrush.springmvcrest.fep;
//
//
//import org.modelmapper.ModelMapper;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.support.ResourceBundleMessageSource;
//import org.springframework.web.client.RestTemplate;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
//
//@Configuration
//public class WebConfig extends WebMvcConfigurerAdapter {
//
//    @Bean
//    public ResourceBundleMessageSource messageSource() {
//        ResourceBundleMessageSource source = new ResourceBundleMessageSource();
//        String[] baseNames = new String[]{"i18n/messages", "i18n/responses"};
//        source.setBasenames(baseNames);  // name of the resource bundle
//        source.setCacheSeconds(1000);
//        source.setUseCodeAsDefaultMessage(true);
//        return source;
//    }
//
//
////    @Bean
////    public ModelMapper modelMapper(){
////        return new ModelMapper();
////    }
//
//    @Bean
//    public RestTemplate restTemplate(){
//        return new RestTemplate();
//    }
//
////    @Override
////    public void addInterceptors(InterceptorRegistry registry) {
////       registry.addInterceptor(new ApplicationUserInterceptor()).addPathPatterns("/core/**");
////    }
//}
