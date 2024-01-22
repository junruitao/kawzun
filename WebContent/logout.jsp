<%
    request.getSession().invalidate();
    response.sendRedirect("home.kwz");
%>

