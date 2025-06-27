/*
  The principal name in the spring_sessions table is currently the
  hash code of the authenticated user object. This isn't helpful when
  we move to the account service as Pathfinder will need to delete sessions
  based on WUA_ID being sent from the IDP. We are forcing a delete of all session
  data so we all new sessions after this release will use the new WUA_ID format and
  hence can be deserialised.
*/
DELETE FROM ${datasource.user}.spring_session_attributes;

DELETE FROM ${datasource.user}.spring_session;