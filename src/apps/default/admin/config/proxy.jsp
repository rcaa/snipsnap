<%@ page import="java.util.*,
                 org.snipsnap.config.Configuration"%>
 <%--
  ** Proxy Settings
  ** @author Matthias L. Jugel
  ** @version $Id$
  --%>

<%@ taglib uri="http://java.sun.com/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt" prefix="fmt" %>
<script type="text/javascript" language="Javascript">
  <!--
  function disableOnCheck(checkbox) {
      document.getElementById("app.real.host").disabled = checkbox.checked;
      document.getElementById("app.real.port").disabled = checkbox.checked;
  }
  -->
</script>

<table>
  <tr>
    <td><fmt:message key="config.app.real.autodetect.text"/></td>
    <td>
      <fmt:message key="config.app.real.autodetect"/><br/>
      <input onClick="disableOnCheck(this);" type="checkbox" name="app.real.autodetect" <c:if test="${config.realAutodetect == 'true'}">checked=checked</c:if>>
      <div class="hint">(<fmt:message key="config.detected"/>: <c:out value="${config.url}"/>)</div>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.real.host.text"/></td>
    <td>
      <fmt:message key="config.app.real.host"/><br/>
      <input <c:if test="${config.realAutodetect == 'true'}">disabled="disabled"</c:if>
        id="app.real.host" type="text" name="app.real.host" size="40"
        value="<c:out value='${config.properties["app.real.host"]}'/>">
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.real.port.text"/></td>
    <td>
      <fmt:message key="config.app.real.port"/><br/>
      <input <c:if test="${config.realAutodetect == 'true'}">disabled="disabled"</c:if>
        id="app.real.port" type="text" name="app.real.port" size="40"
        value="<c:out value='${config.properties["app.real.port"]}'/>">
      <c:if test="${!empty errors['app.real.port']}"><img src="images/attention.jpg"></c:if>
    </td>
  </tr>
  <tr>
    <td><fmt:message key="config.app.real.path.text"/></td>
    <td>
      <fmt:message key="config.app.real.path"/><br/>
      <input type="text" name="app.real.path" size="40" value="<c:out value='${config.properties["app.real.path"]}'/>">
    </td>
  </tr>
</table>