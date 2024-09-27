package org.data.sofa.response;

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
