package de.thecodelabs.versionizer.spring;

import de.thecodelabs.versionizer.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UpdateScheduledService
{
	private final UpdateService updateService;

	@Autowired
	public UpdateScheduledService(UpdateService updateService)
	{
		this.updateService = updateService;
	}

	@Scheduled(cron = "${versionizer.service.cron}")
	public void updateSearchTask() {
		System.out.println(updateService.isUpdateAvailable());
	}
}
