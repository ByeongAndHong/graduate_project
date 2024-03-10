from transformers import BertForSequenceClassification
from transformers import BertTokenizer
from keras.preprocessing.sequence import pad_sequences
import torch
import numpy as np

class EmotionClassifier:
    def __init__(self, model_path):
        self.tokenizer = BertTokenizer.from_pretrained('bert-base-multilingual-cased', do_lower_case=False)
        self.model = BertForSequenceClassification.from_pretrained("bert-base-multilingual-cased", num_labels=4)
        self.model.cpu()
        self.model.load_state_dict(torch.load(model_path, map_location='cpu'), strict=False)

    def convert_input_data(self, sentences):
        tokenized_texts = [self.tokenizer.tokenize(sent) for sent in sentences]

        MAX_LEN = 128

        input_ids = [self.tokenizer.convert_tokens_to_ids(x) for x in tokenized_texts]
        input_ids = pad_sequences(input_ids, maxlen=MAX_LEN, dtype="long", truncating="post", padding="post")

        attention_masks = []

        for seq in input_ids:
            seq_mask = [float(i > 0) for i in seq]
            attention_masks.append(seq_mask)

        inputs = torch.tensor(input_ids)
        masks = torch.tensor(attention_masks)

        return inputs, masks

    def test_sentences(self, sentences):
        self.model.eval()

        inputs, masks = self.convert_input_data(sentences)

        b_input_ids = inputs.cpu()
        b_input_mask = masks.cpu()

        with torch.no_grad():
            outputs = self.model(b_input_ids,
                                 token_type_ids=None,
                                 attention_mask=b_input_mask)

        logits = outputs[0]
        logits = logits.detach().cpu().numpy()

        return logits

    def get_test_pred(self, logits):
        temp = np.argmax(logits)
        if temp==0:
            return 1
        elif temp==1:
            return 2
        elif temp==2:
            return 4
        else:
            return 5