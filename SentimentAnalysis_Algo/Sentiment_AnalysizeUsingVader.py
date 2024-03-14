from flask import Flask, jsonify
import requests
from vaderSentiment.vaderSentiment import SentimentIntensityAnalyzer

app = Flask(__name__)

@app.route('/analyzed-comments')
def get_analyzed_comments():
    url = "http://localhost:8080/java/App/latestComments"
    params = {
        "pageId": "284800734697134",
        "count": 5
    }

    response = requests.get(url, params=params)
    analyzer = SentimentIntensityAnalyzer()

    if response.status_code == 200:
        comments = response.json()

        for comment in comments:
            sentiment_scores = analyzer.polarity_scores(comment['message'])
            compound_score = sentiment_scores['compound']
            if compound_score >= 0.05:
                sentiment_label = 'positive'
            elif compound_score <= -0.05:
                sentiment_label = 'negative'
            else:
                sentiment_label = 'neutral'

            comment['sentiment'] = {
                'compound': compound_score,
                'label': sentiment_label
            }
        result = {'comments': comments}  # Wrap the comments in a JSON object
    else:
        return jsonify({"error": f"Error: {response.status_code}"}), response.status_code

    return jsonify(result)

if __name__ == '__main__':
    app.run()