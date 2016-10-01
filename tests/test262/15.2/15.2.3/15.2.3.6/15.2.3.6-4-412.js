  function testcase() 
  {
    var appointment = {
      
    };
    Object.defineProperty(appointment, "startTime", {
      value : 1001,
      writable : true,
      enumerable : true,
      configurable : true
    });
    Object.defineProperty(appointment, "name", {
      value : "NAME",
      writable : true,
      enumerable : true,
      configurable : true
    });
    var meeting = Object.create(appointment);
    Object.defineProperty(meeting, "conferenceCall", {
      value : "In-person meeting",
      writable : true,
      enumerable : true,
      configurable : true
    });
    var teamMeeting = Object.create(meeting);
    var hasOwnProperty = ! teamMeeting.hasOwnProperty("name") && ! teamMeeting.hasOwnProperty("startTime") && ! teamMeeting.hasOwnProperty('conferenceCall');
    return hasOwnProperty && teamMeeting.name === "NAME" && teamMeeting.startTime === 1001 && teamMeeting.conferenceCall === "In-person meeting";
  }
  {
    var __result1 = testcase();
    var __expect1 = true;
  }
  