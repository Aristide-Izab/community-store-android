# Faculty Feature Package

Faculty uses the same screens as Students. Each Faculty activity extends the Student one
(for example `FacultyMarketActivity extends StudentMarketActivity`), and colours, labels and
data follow the signed-in role, so there is no duplicated code.

Faculty-only parts:
- `FacultyCommunityBoardActivity.java` - the community board with a Post button
- `FacultyAddPostActivity.java` - publish an event or announcement (also used by Residence admins)
