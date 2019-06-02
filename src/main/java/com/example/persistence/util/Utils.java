package com.example.persistence.util;

import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;

import org.springframework.stereotype.Component;

/**
 * 
 * @author amit
 *
 */
@Component
public class Utils {

	public Integer getAgeFromDob(Calendar dob) {
		if (dob == null) {
			return null;
		}
		Period period = Period.between(
				LocalDate.of(dob.get(Calendar.YEAR), dob.get(Calendar.MONTH), dob.get(Calendar.DAY_OF_MONTH)),
				LocalDate.now());
		System.out.println(period);
		return period.getYears();
	}

}
