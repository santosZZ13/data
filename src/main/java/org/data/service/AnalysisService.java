package org.data.service;

import org.data.dto.analysis.GetAnalysisDto;

import java.util.Date;

public interface AnalysisService {
	GetAnalysisDto.Response getAnalysisByDate(Date date);
}
