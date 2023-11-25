from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://root:1234@localhost:3306/chatapp'
db = SQLAlchemy(app)

class User(db.Model):
    Id = db.Column(db.Integer, primary_key=True)
    UserName = db.Column(db.String(255), nullable=False)
    Password = db.Column(db.String(255), nullable=False)
    Email = db.Column(db.String(255), unique=True, nullable=False)
    Image = db.Column(db.LargeBinary)

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

with app.app_context():
    db.create_all()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)