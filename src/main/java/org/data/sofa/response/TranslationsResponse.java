package org.data.sofa.response;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class TranslationsResponse {
	private String ar;
	private String ru;
}
