package org.data.response.sf;

import lombok.*;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FieldTranslationsResponse {
	private TranslationsResponse nameTranslation;
	private TranslationsResponse shortNameTranslation;
}
