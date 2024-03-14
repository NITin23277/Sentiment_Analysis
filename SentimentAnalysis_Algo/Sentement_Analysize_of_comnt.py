from flask import Flask, jsonify
import requests
from textblob import TextBlob

app = Flask(__name__)

@app.route('/analyzed-comments')
def get_analyzed_comments():
    url = "http://localhost:8080/java/App/latestComments"
    params = {
        "pageId": "284800734697134",
        "count": 10
    }

    response = requests.get(url, params=params)

    if response.status_code == 200:
        comments = response.json()

        for comment in comments:
            analysis = TextBlob(comment['message'])
            polarity = analysis.sentiment.polarity
            if polarity > 0:
                sentiment_label = 'positive'
            elif polarity < 0:
                sentiment_label = 'negative'
            else:
                sentiment_label = 'neutral'

            comment['sentiment'] = {
                'polarity': polarity,
                'subjectivity': analysis.sentiment.subjectivity,
                'label': sentiment_label
            }
        result = {'comments': comments}  # Wrap the comments in a JSON object
    else:
        return jsonify({"error": f"Error: {response.status_code}"}), response.status_code

    return jsonify(result)

if __name__ == '__main__':
    app.run()
