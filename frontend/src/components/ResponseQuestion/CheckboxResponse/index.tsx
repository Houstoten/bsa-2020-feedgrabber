import {ICheckboxQuestion} from "models/forms/Questions/IQuesion";
import {IQuestionResponse} from "models/IQuestionResponse";
import React, {FC, useEffect, useState} from "react";
import {Checkbox, Input} from "semantic-ui-react";
import styles from "./styles.module.sass";
import {replaceAtIndex} from "../../../helpers/array.helper";
import {IAnswerBody} from '../../../models/forms/Response/types';
import { useTranslation } from "react-i18next";

export interface ICheckboxResponse {
    response?: IAnswerBody;
}

export const CheckboxResponse: FC<IQuestionResponse<ICheckboxQuestion> & ICheckboxResponse> = ({
                                                                                                   question,
                                                                                                   answerHandler,
                                                                                                   response
                                                                                               }) => {
    const isAnswer = (field: string, options: string[]): boolean => {
        if (!options) {
            return false;
        }
        return !!options.find(option => option === field);
    };
    const [boxes, setBoxes] = useState([] as { checked: boolean; value: string }[]);
    const notResponsePage = window.location.pathname.split("/")[1] !== "response";

    useEffect(() => {
        setBoxes(question.details.answerOptions.map(v => ({
            checked: isAnswer(v, (response as { selected: string[]; other: string })?.selected),
            value: v
        })));
        // eslint-disable-next-line
    }, [question]); // in dev only [question]

    const [other, setOther] = useState({
        checked: ((response as { selected: string[]; other: string })?.other && question.details.includeOther),
        value: (response as { selected: string[]; other: string })?.other || ''
    });

    useEffect(() => {
        const boxesChecked = boxes.filter(v => v.checked && v.value);
        answerHandler
            ?.((other.checked && other.value) || boxesChecked.length
                ? {
                    selected: boxesChecked.map(v => v.value),
                    other: (other.checked && other.value) || null
                }
                : null
            );
        // eslint-disable-next-line
    }, [boxes, other]);

    const [t] = useTranslation();

    return (
        <div className={styles.boxes}>
            {boxes.map((v, i) => {
                return <Checkbox disabled={(response !== undefined && !answerHandler) || notResponsePage}
                                 label={v.value}
                                 key={i}
                                 checked={boxes[i].checked}
                                 onChange={() => {
                                     setBoxes(() => {
                                         const {checked, value} = boxes[i];
                                         return replaceAtIndex(boxes, {checked: !checked, value}, i);
                                     });
                                 }
                                 }/>;
            })}
            {question.details.includeOther && (
                <div className={styles.other}>
                    <Checkbox
                        disabled={(response !== undefined && !answerHandler) || notResponsePage}
                        checked={other.checked}
                        onChange={() => {
                            setOther(() => {
                                const {checked, value} = other;
                                return ({checked: !checked, value});
                            });
                        }
                        }/>
                    <Input
                        disabled={response !== undefined && !answerHandler}
                        className={styles.otherInput}
                        defaultValue={other.value}
                        placeholder={t('Other option...')}
                        error={other.checked && !other.value}
                        onChange={(e, {value}) => {
                            setOther(() => {
                                const {checked} = other;
                                return ({checked, value});
                            });
                        }}
                    />
                </div>
            )}
        </div>
    );
};
