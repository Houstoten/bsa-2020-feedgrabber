import {all, call, put, takeEvery} from 'redux-saga/effects';
import {
    addSelectedQuestionsRoutine,
    loadQuestionByIdRoutine,
    loadQuestionsByQuestionnaireRoutine,
    loadQuestionsRoutine,
    saveQuestionToQuestionnaireRoutine
} from './routines';
import apiClient from '../../helpers/apiClient';
import {IGeneric} from 'models/IGeneric';
import {toastr} from 'react-redux-toastr';
import {IQuestion} from "../../models/forms/Questions/IQuesion";
import defaultQuestion from "../../models/forms/Questions/DefaultQuestion";
import question from '../../models/forms/Questions/DefaultQuestion';

function parseQuestion(rawQuestion) {
    return {
        ...rawQuestion,
        type: rawQuestion.type.toLowerCase(),
        details: JSON.parse(rawQuestion.details as string)
    };
}

function* getAll() {
    try {
        const res: IGeneric<IQuestion[]> = yield call(apiClient.get, `/api/questions`);

        const questions = res.data.data.map(q => parseQuestion(q));

        yield put(loadQuestionsRoutine.success(questions));

    } catch (e) {
        yield put(loadQuestionsRoutine.failure());
        toastr.error("Unable to load questions");
    }
}

function* getById(action) {
    try {
        const id = action.payload;

        if (id === 'empty') {
            put(loadQuestionByIdRoutine.success(defaultQuestion));
            return;
        }

        const res: IGeneric<IQuestion> = yield call(apiClient.get, `/api/questions/${action.payload}`);

        const question = parseQuestion(res.data.data);

        yield put(loadQuestionByIdRoutine.success(question));

    } catch (e) {
        yield put(loadQuestionByIdRoutine.failure(e.data.error));
        toastr.error("Unable to load question");
    }
}

function* addFromExisting(action) {
    // payload: {questionnaireId; questions}}
    try {
        const res: IGeneric<IQuestion[]> = yield call(apiClient.patch, `/api/questions`, action.payload);

        const questions = res.data.data.map(q => parseQuestion(q));

        yield put(addSelectedQuestionsRoutine.success(questions));

    } catch (e) {
        yield put(addSelectedQuestionsRoutine.failure(e.data.error));
        toastr.error("Something went wrong, try again");
    }

}

function* saveOrUpdateQuestion(action) {
    try {
        const res: IGeneric<IQuestion> = action.payload.id
            ? yield call(apiClient.put, `/api/questions`, action.payload)
            : yield call(apiClient.post, `/api/questions`, action.payload);

        yield put(saveQuestionToQuestionnaireRoutine.success(res.data.data));

    } catch (e) {
        yield put(saveQuestionToQuestionnaireRoutine.failure());
        toastr.error("Question wasn't saved");
    }
}

function* getByQuestionnaireId(action) {
  try {
    const response = yield call(apiClient.get,
       `http://localhost:5000/api/questions/questionnaires/${action.payload}`);
    const items = response.data.data;
    const questions: IGeneric<IQuestion[]> = items.map(item => parseQuestion(item));
    yield put(loadQuestionsByQuestionnaireRoutine.success(questions));
  } catch (error) {
    yield put(loadQuestionsByQuestionnaireRoutine.failure(error));
    toastr.error("Unable to load questionnaire's questions");
  }
}

export default function* questionSagas() {
    yield all([
        yield takeEvery(loadQuestionsRoutine.TRIGGER, getAll),
        yield takeEvery(saveQuestionToQuestionnaireRoutine.TRIGGER, saveOrUpdateQuestion),
        yield takeEvery(loadQuestionByIdRoutine.TRIGGER, getById),
        yield takeEvery(loadQuestionsByQuestionnaireRoutine.TRIGGER, getByQuestionnaireId),
        yield takeEvery(addSelectedQuestionsRoutine.TRIGGER, addFromExisting)
    ]);
}
