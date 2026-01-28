package com.reynaud.wonders.controller;

import com.reynaud.wonders.dto.WonderDTO;
import com.reynaud.wonders.entity.WonderEntity;
import com.reynaud.wonders.service.WonderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/wonders")
public class WonderController {

    private final WonderService wonderService;

    public WonderController(WonderService wonderService) {
        this.wonderService = wonderService;
    }

    // Web page
    @GetMapping
    public String wondersPage(Authentication authentication, Model model) {
        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }
        
        List<WonderEntity> wonders = wonderService.getAllWonders();
        model.addAttribute("wonders", wonderService.convertToDTOList(wonders));
        
        return "wonders";
    }

    // REST API endpoints
    @GetMapping("/api")
    @ResponseBody
    public ResponseEntity<List<WonderDTO>> getAllWonders() {
        List<WonderEntity> wonders = wonderService.getAllWonders();
        return ResponseEntity.ok(wonderService.convertToDTOList(wonders));
    }

    @GetMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<WonderDTO> getWonder(@PathVariable Long id) {
        WonderEntity wonder = wonderService.getWonderById(id);
        if (wonder == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(wonderService.convertToDTO(wonder));
    }

    @GetMapping("/api/name/{name}")
    @ResponseBody
    public ResponseEntity<List<WonderDTO>> getWondersByName(@PathVariable String name) {
        List<WonderEntity> wonders = wonderService.getWondersByName(name);
        return ResponseEntity.ok(wonderService.convertToDTOList(wonders));
    }

    @GetMapping("/api/face/{face}")
    @ResponseBody
    public ResponseEntity<List<WonderDTO>> getWondersByFace(@PathVariable String face) {
        List<WonderEntity> wonders = wonderService.getWondersByFace(face);
        return ResponseEntity.ok(wonderService.convertToDTOList(wonders));
    }

    @PostMapping("/api")
    @ResponseBody
    public ResponseEntity<WonderDTO> createWonder(@RequestBody WonderDTO wonderDTO) {
        WonderEntity wonder = wonderService.convertToEntity(wonderDTO);
        WonderEntity created = wonderService.createWonder(wonder);
        return ResponseEntity.status(HttpStatus.CREATED).body(wonderService.convertToDTO(created));
    }

    @PutMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<WonderDTO> updateWonder(@PathVariable Long id, @RequestBody WonderDTO wonderDTO) {
        WonderEntity existing = wonderService.getWonderById(id);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }
        
        wonderDTO.setId(id);
        WonderEntity wonder = wonderService.convertToEntity(wonderDTO);
        WonderEntity updated = wonderService.updateWonder(wonder);
        return ResponseEntity.ok(wonderService.convertToDTO(updated));
    }

    @DeleteMapping("/api/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteWonder(@PathVariable Long id) {
        WonderEntity wonder = wonderService.getWonderById(id);
        if (wonder == null) {
            return ResponseEntity.notFound().build();
        }
        
        wonderService.deleteWonder(id);
        return ResponseEntity.noContent().build();
    }
}
