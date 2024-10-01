package org.mule.extension.javabr.smb.internal;

import org.mule.runtime.extension.api.annotation.Extension;
import org.mule.runtime.extension.api.annotation.Configurations;
import org.mule.runtime.extension.api.annotation.dsl.xml.Xml;
import org.mule.sdk.api.annotation.JavaVersionSupport;
import org.mule.sdk.api.meta.JavaVersion;

/**
 * This is the main class of an extension, is the entry point from which
 * configurations, connection providers, operations
 * and sources are going to be declared.
 */
@Xml(prefix = "javabr-smb")
@Extension(name = "Javabr-smb")
@Configurations(JavabrsmbConfiguration.class)
@JavaVersionSupport({ JavaVersion.JAVA_8, JavaVersion.JAVA_11, JavaVersion.JAVA_17 })
public class JavabrsmbExtension {

}
