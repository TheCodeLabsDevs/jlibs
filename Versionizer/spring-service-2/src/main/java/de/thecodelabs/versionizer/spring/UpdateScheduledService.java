package de.thecodelabs.versionizer.spring;

import de.thecodelabs.versionizer.service.UpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UpdateScheduledService
{
	private final UpdateService updateService;

	private final ApplicationEventPublisher applicationEventPublisher;

	@Autowired
	public UpdateScheduledService(UpdateService updateService, ApplicationEventPublisher applicationEventPublisher)
	{
		this.updateService = updateService;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Scheduled(cron = "${versionizer.service.cron}")
	public void updateSearchTask() {
		updateService.fetchCurrentVersion();
		if (updateService.isUpdateAvailable())
		{
			UpdateAvailableEvent customSpringEvent = new UpdateAvailableEvent(this, updateService);
			applicationEventPublisher.publishEvent(customSpringEvent);
		}
	}
}
