const Koa = require('koa');
const router = require('koa-router')();
const bodyParser = require('koa-bodyparser');
const mongoose = require('mongoose');
const config = require('config');
const passport = require('passport');
const admin = require('firebase-admin');
const serviceAccount = require('./../foxtrotdoorpanel-firebase-adminsdk.json');

const getWorkersController = require('./controllers/workers/get-workers');
const getWorkersByRoomController = require('./controllers/workers/get-workers-by-room');
const createWorkersController = require('./controllers/workers/create');
const changeWorkerStatusController = require('./controllers/workers/change-status');
const sendWorkerMessageController = require('./controllers/workers/send-message');
const getWorkerByIdController = require('./controllers/workers/get-by-id');
const changeWorkerRoomController = require('./controllers/workers/change-room');
const addWorkerTimeslotController = require('./controllers/workers/add-timeslot');
const bookWorkerTimeslotController = require('./controllers/workers/book-timeslot');
const authenticateWorkerController = require('./controllers/workers/auth');

const getTabletsController = require('./controllers/tablets/get-tablets');
const createTabletController = require('./controllers/tablets/create');

const WorkerModel = require('./models/workers');
const TabletModel = require('./models/tablets');

mongoose.connect(config.get('mongo.uri'), { useNewUrlParser: true })
    .then(() => console.log("Successfully connected to db"))
    .catch(err => console.log(`Error while connecting to db: ${err}`));
mongoose.Promise = global.Promise;

admin.initializeApp({
    credential: admin.credential.cert(serviceAccount),
    databaseURL: 'https://foxtrotdoorpanel.firebaseio.com'
});


const app = new Koa();
require('./passport');
app.use(passport.initialize());

router.param('id', (id, ctx, next) => next());
router.param('room', (room, ctx, next) => next());

router.get('/workers/', getWorkersController);
router.get('/workers/room/:room', getWorkersByRoomController);
router.get('/workers/:id/', getWorkerByIdController);
router.post('/workers/', createWorkersController);
router.post('/workers/status', passport.authenticate('jwt', {session: false}),
    changeWorkerStatusController);
router.post('/workers/:id/message', sendWorkerMessageController);
router.post('/workers/room', passport.authenticate('jwt', {session: false}),
    changeWorkerRoomController);
router.post('/workers/:id/timeslot', addWorkerTimeslotController);
router.post('/workers/:id/book', bookWorkerTimeslotController);
router.post('/workers/login/', authenticateWorkerController);
router.get('/test-worker-token/', passport.authenticate('jwt', {session: false}),
    async ctx => {
        ctx.body = "Authenticated route reached";
});

router.get('/tablets/', getTabletsController);
router.post('/tablets/', createTabletController);

app.use(async (ctx, next) => {
    ctx.workerModel = new WorkerModel();
    ctx.tabletModel = new TabletModel();
    ctx.admin = admin;
    await next();
});

app.use(bodyParser());
app.use(router.routes());

app.listen(5000, () => console.log('Server is starting...'));