// This file was generated by Mendix Studio Pro.
//
// WARNING: Only the following code will be retained when actions are regenerated:
// - the import list
// - the code between BEGIN USER CODE and END USER CODE
// - the code between BEGIN EXTRA CODE and END EXTRA CODE
// Other code you write will be lost the next time you deploy the project.
// Special characters, e.g., é, ö, à, etc. are supported in comments.

package email_connector.actions;

import com.mendix.core.Core;
import com.mendix.core.CoreException;
import com.mendix.datahub.connector.eventtracking.Metrics;
import com.mendix.systemwideinterfaces.core.IContext;
import com.mendix.webui.CustomJavaAction;
import email_connector.proxies.*;
import com.mendix.systemwideinterfaces.core.IMendixObject;
import email_connector.proxies.constants.Constants;
import java.util.Map;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.counting;

public class CalculateIncomingAccountMetrics extends CustomJavaAction<java.lang.Void>
{
	/** @deprecated use com.mendix.utils.ListUtils.map(EmailAccountList, com.mendix.systemwideinterfaces.core.IEntityProxy::getMendixObject) instead. */
	@java.lang.Deprecated(forRemoval = true)
	private final java.util.List<IMendixObject> __EmailAccountList;
	private final java.util.List<email_connector.proxies.EmailAccount> EmailAccountList;

	public CalculateIncomingAccountMetrics(
		IContext context,
		java.util.List<IMendixObject> _emailAccountList
	)
	{
		super(context);
		this.__EmailAccountList = _emailAccountList;
		this.EmailAccountList = java.util.Optional.ofNullable(_emailAccountList)
			.orElse(java.util.Collections.emptyList())
			.stream()
			.map(emailAccountListElement -> email_connector.proxies.EmailAccount.initialize(getContext(), emailAccountListElement))
			.collect(java.util.stream.Collectors.toList());
	}

	@java.lang.Override
	public java.lang.Void executeAction() throws Exception
	{
		// BEGIN USER CODE
		for (var protocol : ENUM_IncomingProtocol.values())
		{
			var basicAccCount = this.EmailAccountList.stream()
					.filter(emailAccount -> {
						try {
							return emailAccount.getIncomingEmailConfiguration_EmailAccount().getIncomingProtocol().equals(protocol) && !emailAccount.getisOAuthUsed();
						} catch (CoreException e) {
							Core.getLogger(Constants.getLogNode()).error(e);
						}
						return false;
					})
					.count();

			Map<Boolean, Long> mailBoxTypeAccounts = this.EmailAccountList.stream().filter(emailAccount -> {
				try {
					return emailAccount.getIncomingEmailConfiguration_EmailAccount().getIncomingProtocol().equals(protocol);
				} catch (CoreException e) {
					Core.getLogger(Constants.getLogNode()).error(e);
				}
				return false;
			}).collect(Collectors.groupingBy(EmailAccount::getIsSharedMailbox, counting()));

			Map<ENUM_OAuthType, Long> oAuthAccounts = this.EmailAccountList.stream().filter(emailAccount -> {
				try {
					return emailAccount.getIncomingEmailConfiguration_EmailAccount().getIncomingProtocol().equals(protocol) && emailAccount.getEmailAccount_OAuthProvider() != null;
				} catch (CoreException e) {
					Core.getLogger(Constants.getLogNode()).error(e);
				}
				return false;
			}).collect(Collectors.groupingBy(emailAccount -> {
				try {
					return emailAccount.getEmailAccount_OAuthProvider().getOAuthType();
				} catch (CoreException e) {
					throw new IllegalStateException(e);
				}
			}, counting()));

			Metrics.createGauge("dnl_connectors_ec_account_configuration")
					.addTag("type", protocol.getCaption())
					.addTag("auth_method",  "basic")
					.addTag("setup", "incoming")
					.setDescription("User sets up account configuration")
					.build()
					.recordValue(basicAccCount);
			Metrics.createGauge("dnl_connectors_ec_account_configuration")
					.addTag("type", protocol.getCaption())
					.addTag("auth_method",  ENUM_OAuthType.AUTH_CODE.name().toLowerCase())
					.addTag("setup", "incoming")
					.setDescription("User sets up account configuration")
					.build()
					.recordValue(oAuthAccounts.getOrDefault(ENUM_OAuthType.AUTH_CODE, 0L));
			Metrics.createGauge("dnl_connectors_ec_account_configuration")
					.addTag("type", protocol.getCaption())
					.addTag("auth_method",  ENUM_OAuthType.CLIENT_CRED.name().toLowerCase())
					.addTag("setup", "incoming")
					.setDescription("User sets up account configuration")
					.build()
					.recordValue(oAuthAccounts.getOrDefault(ENUM_OAuthType.CLIENT_CRED, 0L));
			Metrics.createGauge("dnl_connectors_ec_account_configuration")
					.addTag("type", protocol.getCaption())
					.addTag("setup", "incoming")
					.addTag("mailbox_type", "shared")
					.setDescription("User sets up account configuration")
					.build()
					.recordValue(mailBoxTypeAccounts.getOrDefault(Boolean.TRUE, 0L));
			Metrics.createGauge("dnl_connectors_ec_account_configuration")
					.addTag("type", protocol.getCaption())
					.addTag("setup", "incoming")
					.addTag("mailbox_type", "primary")
					.setDescription("User sets up account configuration")
					.build()
					.recordValue(mailBoxTypeAccounts.getOrDefault(Boolean.FALSE, 0L));
		}

		return null;
		// END USER CODE
	}

	/**
	 * Returns a string representation of this action
	 * @return a string representation of this action
	 */
	@java.lang.Override
	public java.lang.String toString()
	{
		return "CalculateIncomingAccountMetrics";
	}

	// BEGIN EXTRA CODE
	// END EXTRA CODE
}