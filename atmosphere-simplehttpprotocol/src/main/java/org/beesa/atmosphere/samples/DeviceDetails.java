package org.beesa.atmosphere.samples;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@XmlRootElement
@XmlType(propOrder = { "username", "uuid"})
public class DeviceDetails {

	@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
	String username;
	@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
	String uuid;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
}
