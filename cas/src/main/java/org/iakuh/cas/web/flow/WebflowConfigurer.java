package org.iakuh.cas.web.flow;

import org.apereo.cas.web.flow.CasWebflowConstants;
import org.apereo.cas.web.flow.DefaultWebflowConfigurer;
import org.iakuh.cas.authentication.TenantRememberMeUsernamePasswordCredential;
import org.iakuh.cas.authentication.TenantUsernamePasswordCredential;
import org.springframework.webflow.definition.registry.FlowDefinitionRegistry;
import org.springframework.webflow.engine.Flow;
import org.springframework.webflow.engine.ViewState;
import org.springframework.webflow.engine.builder.BinderConfiguration;
import org.springframework.webflow.engine.builder.support.FlowBuilderServices;

public class WebflowConfigurer extends DefaultWebflowConfigurer {

  /**
   * Instantiates a new Default webflow configurer.
   *
   * @param flowBuilderServices the flow builder services
   * @param flowDefinitionRegistry the flow definition registry
   */
  public WebflowConfigurer(
      FlowBuilderServices flowBuilderServices,
      FlowDefinitionRegistry flowDefinitionRegistry) {
    super(flowBuilderServices, flowDefinitionRegistry);
  }

  @Override
  protected void createRememberMeAuthnWebflowConfig(final Flow flow) {
    if (casProperties.getTicket().getTgt().getRememberMe().isEnabled()) {
      createFlowVariable(flow, CasWebflowConstants.VAR_ID_CREDENTIAL,
          TenantRememberMeUsernamePasswordCredential.class);
      final ViewState state = (ViewState) flow
          .getState(CasWebflowConstants.STATE_ID_VIEW_LOGIN_FORM);
      final BinderConfiguration cfg = getViewStateBinderConfiguration(state);
      cfg.addBinding(new BinderConfiguration.Binding("rememberMe", null, false));
    } else {
      createFlowVariable(flow, CasWebflowConstants.VAR_ID_CREDENTIAL,
          TenantUsernamePasswordCredential.class);
    }
  }
}
