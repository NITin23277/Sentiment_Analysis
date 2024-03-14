import streamlit as st
import requests

def fetch_api_data(api_url):
    try:
        response = requests.get(api_url)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error(f"Error fetching data: {e}")
        return None

def notify_users(api_url, username):
    try:
        full_url = f"{api_url}?username={username}"
        response = requests.get(full_url)
        response.raise_for_status()
        return response.json()
    except requests.exceptions.RequestException as e:
        st.error(f"Error sending notification: {e}")
        return None

st.title('Comment Sentiment Analysis')

st.sidebar.title("Settings")
username = st.sidebar.text_input("Username to Notify", value="nitin")

COMMENTS_API_URL = "http://localhost:5000/analyzed-comments"  # Corrected the URL
NOTIFY_API_URL = "http://localhost:8080/java/App/notifyUsers"

# Function to display comments and their sentiments
def display_comments(comments):
    for comment in comments:
        sentiment = comment['sentiment']
        st.write(f"**From:** {comment['from']}")
        st.write(f"**Message:** {comment['message']}")
        st.write(f"**Sentiment:** {sentiment['label']} (Polarity: {sentiment['polarity']:.2f}, Subjectivity: {sentiment['subjectivity']:.2f})")
        st.markdown("---")

# Fetch Comments
if st.sidebar.button('Fetch Comments'):
    st.sidebar.text('Fetching comments...')
    data = fetch_api_data(COMMENTS_API_URL)
    st.sidebar.text('Fetch complete!')

    if data and 'comments' in data:
        with st.expander("See comments"):
            display_comments(data['comments'])
    else:
        st.error("No data available or invalid format")

# Notify User
if st.sidebar.button('Notify User'):
    notify_response = notify_users(NOTIFY_API_URL, username)
    if notify_response:
        st.success(f"Notification sent successfully to {username}")
    else:
        st.error("Failed to send notification.")

# Styling
st.markdown("""
    <style>
    .sidebar .sidebar-content {
        background-color: #f1f3f6;
    }
    </style>
    """, unsafe_allow_html=True)
