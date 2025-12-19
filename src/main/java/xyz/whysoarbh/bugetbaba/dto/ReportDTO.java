package xyz.whysoarbh.bugetbaba.dto;

import lombok.Data;

@Data
public class ReportDTO {
    private String email;      // recipient email
    private String startDate;  // optional (YYYY-MM-DD)
    private String endDate;    // optional (YYYY-MM-DD)
}
