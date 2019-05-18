/**
 * 
 * Defines DAO class for this application to access database using JPA.
 * 
 * @author amit
 *
 */
// enables Null constraints check at runtime. This annotation force, all parameters and return
// values must not be null at runtime.
// If we want to exclude this constraint on particular method/method parameter or method return type, then we need to
// EXPLICITLY need to use @Nullable annotation on method (for return value), at method parameter level. 
@org.springframework.lang.NonNullApi
package com.example.persistence.dao.auto;