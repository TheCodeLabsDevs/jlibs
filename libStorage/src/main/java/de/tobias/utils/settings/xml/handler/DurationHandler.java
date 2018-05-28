package de.tobias.utils.settings.xml.handler;

import org.dom4j.Element;

import de.tobias.utils.settings.SettingsSerializable;
import de.tobias.utils.settings.xml.ClassHandler;
import de.tobias.utils.settings.xml.XMLFormatStrings;
import javafx.util.Duration;

@Deprecated
public class DurationHandler implements ClassHandler {

	@Override
	public Element serialize(Element rootElement, Object obj) throws IllegalArgumentException {
		if (obj instanceof Duration) {
			Duration duration = (Duration) obj;
			Element element = rootElement.addElement(getType());
			element.addText(String.valueOf(duration.toMillis()));

			return element;
		}
		return null;
	}

	@Override
	public Class<?> getClassType() {
		return Duration.class;
	}

	@Override
	public String getType() {
		return XMLFormatStrings.DURATION_ELEMENT;
	}

	@Override
	public <T extends SettingsSerializable> Object deserializeClass(Element element) {
		String content = element.getStringValue();
		if (content != null) {
			return Duration.millis(Double.valueOf(content));
		}
		return null;
	}

}
