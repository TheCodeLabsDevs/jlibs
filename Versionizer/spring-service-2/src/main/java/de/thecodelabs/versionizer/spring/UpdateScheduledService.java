package de.thecodelabs.versionizer.spring;

import de.thecodelabs.versionizer.service.UpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class UpdateScheduledService
{
	private final UpdateService updateService;

	private final ApplicationEventPublisher applicationEventPublisher;

	private static final Logger LOG = LoggerFactory.getLogger(UpdateScheduledService.class);

	@Autowired
	public UpdateScheduledService(UpdateService updateService, ApplicationEventPublisher applicationEventPublisher)
	{
		this.updateService = updateService;
		this.applicationEventPublisher = applicationEventPublisher;
	}

	@Scheduled(cron = "${versionizer.service.cron}")
	public void updateSearchTask() {
		try
		{
			updateService.fetchCurrentVersion();
			if(updateService.isUpdateAvailable())
			{
				UpdateAvailableEvent customSpringEvent = new UpdateAvailableEvent(this, updateService);
				applicationEventPublisher.publishEvent(customSpringEvent);
			}
		} catch(NullPointerException e) {
			LOG.debug("Error on update search", e);
		}
	}
}
