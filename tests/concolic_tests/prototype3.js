function TaskControlBlock(link, id, priority, queue, task) {
  this.link = link;
  this.id = id;
  this.priority = priority;
  this.queue = queue;
  this.task = task;
  if (queue == null) {
    this.state = STATE_SUSPENDED;
  } else {
    this.state = STATE_SUSPENDED_RUNNABLE;
  }
}
var STATE_RUNNABLE = 1;
var STATE_SUSPENDED = 2;
var STATE_SUSPENDED_RUNNABLE = STATE_SUSPENDED | STATE_RUNNABLE;

TaskControlBlock.prototype.markAsRunnable = function () {
  this.state = this.state | STATE_RUNNABLE;
};
TaskControlBlock.prototype.checkPriorityAdd = function (task, packet) {
  if (this.queue == null) {
    this.queue = packet;
    this.markAsRunnable();
    if (this.priority > task.priority) return this;
  } else { 
		this.queue = packet.addTo(this.queue);
  }
  return task;
};

function Packet(link, id, kind) {
  this.link = link;
  this.id = id;
  this.kind = kind;
  this.a1 = 0;
  this.a2 = new Array(DATA_SIZE);
}
var DATA_SIZE = 4;
Packet.prototype.addTo = function (queue) {
  this.link = null;
  if (queue == null) return this;
  var peek, next = queue;
  while ((peek = next.link) != null)
    next = peek;
  next.link = this;
  return queue;
};

function HandlerTask(scheduler) {
  this.scheduler = scheduler;
  this.v1 = null;
  this.v2 = null;
}
function Scheduler() {
  this.queueCount = 0;
  this.holdCount = 0;
  this.blocks = new Array(6);
  this.list = null;
  this.currentTcb = null;
  this.currentId = null;
}
var scheduler = new Scheduler();
var packet = new Packet(null, 1, 0);
var t0 = new TaskControlBlock(null, 0, 0, null, new HandlerTask());
var t1 = new TaskControlBlock(null, 1, 1, null, new HandlerTask());
t0.checkPriorityAdd(t1, packet);
