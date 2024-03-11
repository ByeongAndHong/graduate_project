from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from datetime import datetime
from model import EmotionClassifier, AnalysisModel

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root:1234@localhost:3306/chatapp'
db = SQLAlchemy(app)

model_path = 'your_path'
ec = EmotionClassifier(model_path)

api_key = "your_api_key"
al = AnalysisModel(api_key)

class User(db.Model):
    Id = db.Column(db.Integer, primary_key=True)
    UserName = db.Column(db.String(255), nullable=False)
    Password = db.Column(db.String(255), nullable=False)
    Email = db.Column(db.String(255), unique=True, nullable=False)
    Image = db.Column(db.LargeBinary)

class Friend(db.Model):
    Id = db.Column(db.Integer, primary_key=True)
    UserId = db.Column(db.Integer, db.ForeignKey('user.Id'))
    FriendId = db.Column(db.Integer, db.ForeignKey('user.Id'))

class Chatlist(db.Model):
    Id = db.Column(db.Integer, primary_key=True)
    SmallId = db.Column(db.Integer, db.ForeignKey('user.Id'))
    BigId = db.Column(db.Integer, db.ForeignKey('user.Id'))
    Message = db.Column(db.String(255), nullable=False)
    Time = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)

class Chat(db.Model):
    Id = db.Column(db.Integer, primary_key=True)
    ChatId = db.Column(db.Integer, db.ForeignKey('chatlist.Id'))
    UserId = db.Column(db.Integer, db.ForeignKey('user.Id'))
    TargetId = db.Column(db.Integer, db.ForeignKey('user.Id'))
    Message = db.Column(db.String(255), nullable=False)
    Time = db.Column(db.DateTime, default=datetime.utcnow, nullable=False)
    Emotion = db.Column(db.String(255), nullable=True)
    New = db.Column(db.Boolean, default=True, nullable=False)

@app.route('/login', methods=['POST'])
def login():
    data = request.get_json()

    email = data.get('Email')
    password = data.get('Password')

    user = User.query.filter_by(Email=email).first()

    if user:
        if user.Password == password:
            return jsonify({'Status': 'success', 'Id': str(user.Id)})
        else:
            return jsonify({'Status': 'wrong password', 'Id': '-1'})
    else:
        return jsonify({'Status': 'wrong id', 'Id': '-1'})

@app.route('/register', methods=['POST'])
def register():
    data = request.get_json()

    new_user = User(
        UserName=data.get('UserName'),
        Password=data.get('Password'),
        Email=data.get('Email'),
        Image=None
    )

    try:
        with app.app_context():  # Flask 애플리케이션 컨텍스트 설정
            db.session.add(new_user)
            db.session.commit()

        return jsonify({'Status': 'success'})
    except Exception as e:
        return jsonify({'Status': 'error'})

@app.route('/send', methods=['POST'])
def send():
    data = request.get_json()
    UserId = data.get('UserId')
    TargetId = data.get('TargetId')
    Message = data.get('Message')

    if UserId > TargetId:
        SmallId = TargetId
        BigId = UserId
    else:
        SmallId = UserId
        BigId = TargetId

    try:
        with app.app_context():  # Flask 애플리케이션 컨텍스트 설정
            chat_list_record = Chatlist.query.filter_by(SmallId=SmallId, BigId=BigId).first()

            if not chat_list_record:
                new_chat_list_record = Chatlist(SmallId=SmallId, BigId=BigId, Message=Message)
                db.session.add(new_chat_list_record)
                db.session.commit()

                # 새로 생성한 ChatList 레코드의 ID를 가져옴
                ChatId = new_chat_list_record.Id
                Time = new_chat_list_record.Time
                new_message = Chat(
                    ChatId=ChatId,
                    UserId=UserId,
                    TargetId=TargetId,
                    Message=Message,
                    Time=Time
                )

                db.session.add(new_message)
                db.session.commit()

                return jsonify({'Status': 'success', 'ChatId': ChatId})
            else:
                # ChatList 레코드가 이미 존재하면 해당 레코드의 ID를 가져옴
                Time = datetime.utcnow()
                ChatId = chat_list_record.Id
                new_message = Chat(
                    ChatId=ChatId,
                    UserId=UserId,
                    TargetId=TargetId,
                    Message=Message,
                    Time=Time
                )

                db.session.add(new_message)
                chat_list_record.Message = Message
                chat_list_record.Time = Time
                db.session.commit()

                return jsonify({'Status': 'success', 'ChatId': ChatId})

    except Exception as e:
        return jsonify({'Status': 'error'})


@app.route('/friends/<int:user_id>', methods=['GET'])
def get_friends(user_id):
    user = User.query.get(user_id)
    if not user:
        return jsonify({'Status': 'error', 'Message': 'User not found'})

    friend_ids = [friend.FriendId for friend in Friend.query.filter_by(UserId=user.Id).all()]
    friends = User.query.filter(User.Id.in_(friend_ids)).all()
    friends_list = []
    for friend in friends:
        small_id = min(user_id, friend.Id)
        big_id = max(user_id, friend.Id)
        chat_list_record = Chatlist.query.filter_by(SmallId=small_id, BigId=big_id).first()
        chat_id = chat_list_record.Id if chat_list_record else 0

        friends_list.append({'ChatId': chat_id, 'Id': friend.Id, 'UserName': friend.UserName, 'Email': friend.Email, 'Image': friend.Image})

    return jsonify({'Status': 'success', 'Friends': friends_list})

@app.route('/chatlist/<int:user_id>', methods=['GET'])
def get_chats(user_id):
    inSmallIds = Chatlist.query.filter_by(SmallId=user_id).all()
    inBigIds = Chatlist.query.filter_by(BigId=user_id).all()

    # 각 레코드의 BigId를 사용하여 User 테이블에서 UserName 가져와서 추가
    result = [
        {'ChatId': chat.Id, 'UserId': chat.BigId, 'UserName': User.query.filter_by(Id=chat.BigId).first().UserName, 'Message': chat.Message,
         'Time': chat.Time} for chat in inSmallIds]

    # 각 레코드의 SmallId를 사용하여 User 테이블에서 UserName 가져와서 추가
    result += [{'ChatId': chat.Id, 'UserId': chat.SmallId, 'UserName': User.query.filter_by(Id=chat.SmallId).first().UserName,
                'Message': chat.Message, 'Time': chat.Time} for chat in inBigIds]

    # 결과를 Time으로 최신순으로 정렬
    sorted_result = sorted(result, key=lambda x: x['Time'], reverse=True)

    # 정렬 후 Time 필드 제거
    final_result = [{'ChatId': chat['ChatId'], 'UserId': chat['UserId'], 'UserName': chat['UserName'], 'Message': chat['Message']} for chat in sorted_result]
    return jsonify({'Status': 'success', 'Chats': final_result})

@app.route('/message/<int:chat_id>', methods=['GET'])
def get_messages(chat_id):
    if chat_id == 0:
        messages = []
        return jsonify({'Status': 'success', 'Messages': messages})

    messages = Chat.query.filter_by(ChatId=chat_id).all()

    # 각 레코드의 BigId를 사용하여 User 테이블에서 UserName 가져와서 추가
    result = [{'UserId': message.UserId, 'Message': message.Message, 'Time': message.Time} for message in messages]

    # 결과를 Time으로 최신순으로 정렬
    sorted_result = sorted(result, key=lambda x: x['Time'], reverse=False)

    # 정렬 후 Time 필드 제거
    final_result = [{'UserId': message['UserId'], 'Message': message['Message']} for message in sorted_result]
    return jsonify({'Status': 'success', 'Messages': final_result})

@app.route('/emotion/<int:chat_id>/<int:user_id>/<int:friend_id>', methods=['GET'])
def get_emotion(chat_id, user_id, friend_id):
    if chat_id == 0:
        return jsonify({'Percent': 50, 'Analysis': ""})
    messages = Chat.query.filter_by(ChatId=chat_id, UserId=friend_id).order_by(Chat.Time).all()
    message_string = '. '.join([str(message.Message) for message in messages])
    logit = ec.test_sentences([message_string])
    predict = ec.get_test_pred(logit)
    percent = 20*predict

    messages = Chat.query.filter_by(ChatId=chat_id).order_by(Chat.Time).all()
    message_string = al.configure_messages(messages, user_id)
    analysis = al.analysis_messages(message_string)
    print(percent)
    print(analysis)
    return jsonify({'Percent': percent, 'Analysis': analysis})

with app.app_context():
    db.create_all()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)