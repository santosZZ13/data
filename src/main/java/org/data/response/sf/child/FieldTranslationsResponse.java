package org.data.response.sf.child;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FieldTranslationsResponse {
	private NameTranslationResponse nameTranslation;
	private NameTranslationResponse shortNameTranslation;
}