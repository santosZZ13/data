package org.data.service.ex;

import org.data.dto.ImportMatchesJsonFile;
import org.springframework.web.multipart.MultipartFile;

public interface ExService {
	ImportMatchesJsonFile.Response getDataFile(MultipartFile request);
}
