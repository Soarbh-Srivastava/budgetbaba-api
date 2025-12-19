package xyz.whysoarbh.bugetbaba.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.whysoarbh.bugetbaba.service.DashBoardService;

import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashBoardController
{
    private final DashBoardService dashBoardService;

    @GetMapping
    public ResponseEntity<Map<String,Object>> getDashBoardData()
    {
        Map<String,Object> dashBoardData = dashBoardService.getDashBoardData();
        return ResponseEntity.ok(dashBoardData);
    }
}
