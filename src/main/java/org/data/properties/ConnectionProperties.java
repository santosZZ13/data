package org.data.properties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Component
@ConfigurationProperties(prefix = "connection", ignoreInvalidFields = true)
@Data
public class ConnectionProperties {

	private EightXBet eightXBet;
	private SofaScore sofasocre;
	private Local local;

	@EqualsAndHashCode(callSuper = true)
	@Data
	public static class EightXBet extends ServerConnection {

	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	public static class SofaScore extends ServerConnection {

	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	public static class Local extends ServerConnection {

	}

	@Data
	public static class ServerConnection {
		private String url;
		private Map<String, String> headers;
	}

	@EqualsAndHashCode(callSuper = true)
	@Data
	public static class BasicAuthConnection extends ServerConnection {
		private String username;
		private String password;
	}

	public ServerConnection getHost(Host host) {
		return switch (host) {
			case EIGHTXBET -> this.eightXBet;
			case SOFASCORE -> this.sofasocre;
			case LOCAL -> this.local;
			default -> null;
		};
	}


	public enum Host {
		EIGHTXBET,
		SOFASCORE,
		LOCAL;

		public static Optional<Host> lookup(String host) {
			return Stream.of(values())
					.filter(value -> value.name().equalsIgnoreCase(host))
					.findFirst();
		}
	}
}
