package com.example.route.controller;

import com.example.route.dto.RouteRequest;
import com.example.route.dto.RouteResponse;
import com.example.route.service.RouteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
public class RouteController {

    private final RouteService routeService;

    @Autowired
    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("routeRequest", new RouteRequest());
        return "index";
    }

    @PostMapping("/api/route")
    @ResponseBody
    public ResponseEntity<RouteResponse> getOptimizedRoute(@Valid @RequestBody RouteRequest request, 
                                                          BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest()
                    .body(RouteResponse.error("Validation error: " + bindingResult.getAllErrors().get(0).getDefaultMessage()));
        }

        RouteResponse response = routeService.getOptimizedRoute(request);
        
        if ("ERROR".equals(response.getStatus())) {
            return ResponseEntity.badRequest().body(response);
        }
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/route")
    public String getRoute(@Valid @ModelAttribute("routeRequest") RouteRequest request, 
                          BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "index";
        }

        RouteResponse response = routeService.getOptimizedRoute(request);
        model.addAttribute("routeResponse", response);
        model.addAttribute("routeRequest", request);
        
        return "index";
    }
} 