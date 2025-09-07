package org.data.controller;

import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.data.dto.lottery.GetLotteryResultPredictDto;
import org.data.dto.lottery.PostLotteryResultPredictDto;
import org.data.dto.lottery.SaveLotteryDto;
import org.data.service.LotteryService;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/lottery")
@CrossOrigin(origins = "http://localhost:3000")
@Log4j2
public class LotteryController {

	private final LotteryService lotteryService;

	@PostMapping("/save")
	public SaveLotteryDto.Response saveLotteries(@RequestBody SaveLotteryDto.Request request) {
		log.info("Received request to save lotteries with {} lotteries", request.getLotteries().size());
		return lotteryService.saveLotteries(request);
	}

	@PostMapping("/savePredict")
	public PostLotteryResultPredictDto.Response predictResults(@RequestBody PostLotteryResultPredictDto.Request request) {
		log.info("Received request to predict lottery results");
		return lotteryService.predictResults(request);
	}

	@GetMapping("/predict")
	public GetLotteryResultPredictDto.Response predictResults() {
		log.info("Received request to get predicted lottery results");
		return lotteryService.predictResults();
	}
}
